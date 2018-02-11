/**
 * 表格
 * attr:
 * gridConfig: {
            multiSelect: 是否多选,
            multiPage:是否分页
            pageSize:分页时每页数据量
            dataSrc:{
                url: 请求地址,
                param: 请求参数
             }表格数据源,设置后表格数据从服务器获取,而不从data获取 ,
            data:表格数据,设置dataSrc后该属性无效
            columnDefs: [
                    {field: 对应data字段, displayName: 表头, width: 列宽, enableCellEdit: 是否可编辑, cellTemplate:单元格模板},
                ]列定义,
            loadGridSerivce:加载表格服务
        };
 gridService:{
 insertNewEntity() :插入新空行
 registerEvent (name, fn) :注册表格事件
 getRegisterEvent (name) :获取注册的事件
 addNewEntity (rowEntity) :添加新行
 saveEntities (savePromise):保存表格数据
        saveEntities(function(entities){
            //保存
        })
 getSelectedEntities :获取选中的行
 cleanData:清空表格数据
 deleteSelectedEntities(deletePromise):删除选中的数据
        deleteSelectedEntities(function(entities){
            //删除
        })
 refreshGrid(onDataLoad):刷新表格

}

 自定义单元格时添加事件:
 gridConfig: {
          ..........
            columnDefs: [{
                    //绑定事件
                    cellTemplate: '<a href="javascript:void(0)" ng-click="grid.appScope.doEvent(row.entity)">查看</a>'
                           }],
            loadGridSerivce: function (gridService) {
                //注册事件
                gridService.registerEvent('doEvent', function (entity) {
                    ..........
                });
            },
          ..........
        };

 对 row 的操作:
 row.setThisRowInvisible 将本行隐藏
 table内容可复制:
 ui-grid.js 注释第15375行内容：evt.preventDefault()
 ui-grid.min.js 找到上述函数，删除函数体中内容： a.preventDefault()
 */
define([
    'lib/angular-ui-grid/ui-grid.min'
], function () {
    module.addCSS('lib/angular-ui-grid/ui-grid.css');
    module.addRequires([
        'ui.grid',
        'ui.grid.pagination',
        'ui.grid.edit',
        'ui.grid.cellNav',
        'ui.grid.selection',
        'ui.grid.resizeColumns',
        'ui.bootstrap'
    ]);
    var headerTemplate = "<div class=\"ui-grid-header custom-ui-grid-header\">\n" +
        "    <div class=\"ui-grid-top-panel\">\n" +
        "        <div class=\"ui-grid-header-viewport\">\n" +
        "            <div class=\"ui-grid-header-canvas\" style=\"width: 100%\">\n" +
        "                <div class=\"ui-grid-header-cell-wrapper\" style=\"display: block;\" ng-style=\"colContainer.headerCellWrapperStyle()\">\n" +
        "                    <div class=\"ui-grid-header-cell-row\" style=\"display: block; border-bottom: 1px solid;border-bottom-color: #d4d4d4;\">\n" +
        "                        <div  class=\"ui-grid-header-cell\" ng-repeat=\"superCol in grid.options.superColDefs track by $index\" col-name=\"{{superCol.name}}\">\n" +
        "                            <div class=\"ui-grid-cell-contents\" data-ng-bind=\"superCol.displayName\">\n" +
        "                            </div>\n" +
        "                        </div>\n" +
        "                    </div>\n" +
        "                    <div class=\"ui-grid-header-cell-row\">\n" +
        "                        <div class=\"ui-grid-header-cell ui-grid-clearfix\"\n" +
        "                             ng-repeat=\"col in colContainer.renderedColumns track by col.colDef.name\"\n" +
        "                             ui-grid-header-cell col=\"col\" super-col-width-update render-index=\"$index\">\n" +
        "                        </div>\n" +
        "                    </div>\n" +
        "                </div>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "    </div>\n" +
        "</div>";
    module.directive('superColWidthUpdate', ['$timeout', function ($timeout) {
        return {
            'restrict': 'A',
            'link': function (scope, element) {
                if (!scope.$parent.grid.gridResize) {
                    scope.$parent.grid.gridResize = function (column) {
                        _updateSuperColWidth(column, false);
                    }
                }
                element.prop('col', scope.col);
                element[0].addEventListener("DOMNodeRemoved", function () {
                    _updateSuperColWidth($(this).prop('col'), true);
                });

                var _updateSuperColWidth = function (column, remove, newColumn) {
                    if (!column || !column.visible || (!newColumn && !remove) && (column.lastUpdateWidth && column.lastUpdateWidth == column.drawnWidth)) {
                        return;
                    }
                    column.lastUpdateWidth = column.drawnWidth;
                    $timeout(function () {
                        var _colId = column.colDef.superCol, uid = column.uid;

                        var _parentCol = jQuery('.ui-grid-header-cell[col-name="' + _colId + '"]');
                        var _parentWidth = 0;

                        var children = _parentCol.prop('_children');
                        if (!children) {
                            children = {};
                            _parentCol.prop('_children', children);
                        }
                        if (remove) {
                            delete children[uid];
                        } else {
                            children[uid] = column.drawnWidth;
                        }
                        for (var child in children) {
                            _parentWidth += children[child];
                        }

                        _parentCol.css({
                            'min-width': _parentWidth + 'px',
                            'max-width': _parentWidth + 'px',
                            'text-align': "center"
                        });
                    }, 0);
                };
                _updateSuperColWidth(scope.col, false, true);
            }
        };
    }]);

    module.directive('ncGrid', function () {
        var defaultOptions = {
            enableSorting: false,
            enableColumnMenus: false,
            enablePaginationControls: false,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            enableFullRowSelection: false,
            multiSelect: false,
            modifierKeysToMultiSelect: false,
            noUnselect: true,
            paginationMaxSize: 4
        };

        var ROW_TYPE_NEW = 1, ROW_TYPE_MODIFY = 2;

        function NCGridService(scope) {
            this.scope = scope;
        }

        NCGridService.prototype.insertNewEntity = function () {
            var columns = this.scope.gridConfig.columnDefs;
            var newEntity = {};
            for (var n = 0, length = columns.length; n < length; n++) {
                newEntity[columns[n].field] = null;
            }
            return this.addNewEntity(newEntity);
        };

        NCGridService.prototype.registerEvent = function (name, fn) {
            this.scope.gridConfig.appScopeProvider[name] = fn;
        };

        NCGridService.prototype.getRegisterEvent = function (name) {
            return this.scope.gridConfig.appScopeProvider[name];
        };

        NCGridService.prototype.addNewEntity = function (rowEntity) {
            var scope = this.scope;
            if (scope.currentPage != scope.numPages) {
                this.refreshGrid(function () {
                    pushData(rowEntity);
                });
            } else {
                pushData(rowEntity);
            }
            function pushData(rowEntity) {
                var data = scope.gridConfig.data;
                data.push(rowEntity);
                rowEntity.$rowType = ROW_TYPE_NEW;
                scope.timeout(function () {
                    scope.gridApi.core.scrollToIfNecessary(scope.gridApi.grid.rows[scope.gridApi.grid.rows.length - 1], scope.gridConfig.columnDefs[0]);
                });
            }

            return rowEntity;
        };

        NCGridService.prototype.saveEntities = function (savePromise) {
            var data = this.scope.gridApi.grid.options.data;
            var entities = [];
            for (var n = 0, length = data.length; n < length; n++) {
                if (data[n].$rowType == ROW_TYPE_NEW || data[n].$rowType == ROW_TYPE_MODIFY) {
                    entities.push(data[n]);
                }
            }
            savePromise(entities);
        };

        NCGridService.prototype.getSelectedRows = function () {
            return this.scope.gridApi.selection.getSelectedGridRows();
        };

        NCGridService.prototype.getSelectedEntities = function () {
            return this.scope.gridApi.selection.getSelectedRows();
        };

        NCGridService.prototype.registe = function (name, func) {
            this.scope[name] = func;
        };
        NCGridService.prototype.cleanData = function () {
            this.scope.totalItems = 0;
            this.scope.numPages = 1;
            this.scope.currentPage = 1;
            this.scope.dataSrc = null;
            this.scope.data.length = 0;
            this.scope.gridConfig.data.length = 0;
        };

        NCGridService.prototype.deleteSelectedEntities = function (deletePromise) {
            var selectedEntities = this.getSelectedEntities();
            var data = this.scope.gridApi.grid.options.data;
            for (var n = 0, length = selectedEntities.length; n < length; n++) {
                var index = data.indexOf(selectedEntities[n]);
                data.splice(index, 1);
            }
            deletePromise(selectedEntities);
        };

        NCGridService.prototype.refreshGrid = function (onDataLoad) {
            var headers = {}, scope = this.scope;
            var options = scope.gridApi.grid.options;
            if (!options.dataSrc) {
                return true;
            }

            if (scope.loading) {
                return false;
            }
            setPageSize(scope);
            if (scope.multiPage) {
                headers['Multi-Page'] = true;
                headers['Current-Page'] = scope.currentPage;
                headers['Page-Size'] = scope.itemsPerPage
            }

            scope.http.get(options.dataSrc.url + '?' + encode(options.dataSrc.param), {
                headers: headers
            }).success(function (data, status, header) {
                scope.loading = false;
                angular.copy(data, options.data);
                scope.totalItems = header('Total-Item-Size');
                if (onDataLoad) {
                    onDataLoad(options.data);
                }
            });
            return true;
        };

        function setPageSize(scope) {
            var options = scope.gridConfig;
            if (!options.multiPage) {
                return;
            }

            if (options.pageSize && options.pageSize > 0) {
                scope.itemsPerPage = options.pageSize;
            } else {
                scope.itemsPerPage = 10;
            }
        }

        return {
            restrict: 'E',
            scope: {
                gridConfig: '=gridConfig'
            },
            template: '<div style="{{gridStyle}}" ui-grid="gridConfig" ui-grid-pagination ui-grid-edit ui-grid-cellNav ui-grid-selection ui-grid-resize-columns/>' +
            '<div class="pagination-container" ng-show="multiPage"><uib-pagination ng-style="paginationCursor" previous-text="上一页" next-text="下一页" total-items="totalItems" ng-change="pageChanged()" ng-model="currentPage" ' +
            'num-pages="numPages" items-per-page= "itemsPerPage" max-size="maxSize" rotate="true"></uib-pagination><span class="pageSize" style="position: relative;top: -20px;left: 10px;">第{{currentPage}}页/共{{numPages}}页</span></div>',
            link: function (scope, element, attrs) {
                if (attrs.style) {
                    scope.gridStyle = attrs.style;
                }
            },
            controller: function ($scope, $state, $http, $timeout) {
                $scope.http = $http;
                $scope.timeout = $timeout;
                var gridConfig = $scope.gridConfig = angular.extend({}, defaultOptions, $scope.gridConfig);

                var ngGridService = null;
                if (!gridConfig.data) {
                    gridConfig.data = $scope.data = [];
                }
                if (gridConfig.multiSelect) {
                    gridConfig.noUnselect = false;
                    gridConfig.enableRowHeaderSelection = true;
                }

                if (!gridConfig.dataSrc) {
                    gridConfig.dataSrc = null;
                }
                $scope.$watch('gridConfig.dataSrc', function (newValue, oldValue) {
                    if (newValue) {
                        $scope.currentPage = 1;
                        ngGridService.refreshGrid(ngGridService.onDataLoad);
                    }
                });

                gridConfig.headerTemplate = headerTemplate;
                gridConfig.multiPage && ($scope.multiPage = true);
                $scope.maxSize = gridConfig.paginationMaxSize;
                $scope.totalItems = 0;
                $scope.numPages = 1;
                $scope.currentPage = 1;
                $scope.pageChanged = function () {
                    if (ngGridService.refreshGrid(ngGridService.onDataLoad) && gridConfig.pageChanged) {
                        gridConfig.pageChanged($scope.currentPage);
                    }
                };

                gridConfig.rowTemplate = "<div ng-dblclick=\"grid.appScope.rowDbClick(row)\" ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" class=\"ui-grid-cell\" ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell ></div>"
                gridConfig.enableCellEditOnFocus = true;
                gridConfig.appScopeProvider = {};
                gridConfig.onRegisterApi = function (gridApi) {
                    $scope.gridApi = gridApi;
                    ngGridService = new NCGridService($scope);
                    gridApi.edit.on.afterCellEdit($scope, function (rowEntity, colDef, newValue, oldValue) {
                        if (newValue != oldValue && rowEntity.$rowType != ROW_TYPE_NEW) {
                            rowEntity.$rowType = ROW_TYPE_MODIFY;
                        }
                        if (ngGridService.getRegisterEvent('cellEdit')) {
                            ngGridService.getRegisterEvent('cellEdit')(rowEntity, gridConfig.data, newValue, oldValue);
                        }
                    });
                    if (gridConfig.loadGridSerivce) {
                        gridConfig.loadGridSerivce(ngGridService);
                    }
                    ngGridService.refreshGrid();
                };
            }
        };
    });
});

