define([], function () {
    module.service('commonService', function ($http) {
        this.getLocationByCompanyCode = function (companyCode) {
            return companyCode;
        };

        /**
         * 获取字符串类型的结果
         *
         * @param attr 需要获取的属性名
         * @param arr 传入的数组对象
         * @returns {string}
         */
        this.getResult = function(attr, arr) {
            if (arr.length == 0) {
                console.log("数组对象不可为空！");
            } else if(typeof (attr) != "string" || typeof (arr) != "object") {
                console.log("参数有误！");
            } else if (!arr[0].hasOwnProperty(attr)) {
                console.log(arr + " 的对象中不包含 " + attr + " 属性！");
            } else {
                var result = '';
                angular.forEach(arr, function(value, index){
                    var end = index == arr.length-1 ? "" : ",";
                    result += value[attr] + end;
                });
                return result;
            }
        };

        /***
         * 获取指定月份的最小时间
         * @param date   日期
         * @param num    月份差值
         * @returns {Date}
         */
        this.actualMinimumDate = function (date, num) {
            var year = date.getFullYear();
            var month = date.getMonth() + num;
            year = parseInt(year + month / 12);
            month = month % 12;
            if (month < 0) {
                month += 12;
            }
            return new Date(year, month, 1);
        };

        /**
         * 获取月份差
         * @param date1
         * @param date2
         * @returns {number}
         */
        this.monthsBetween = function (date1, date2) {
            var year1 = date1.getFullYear();
            var month1 = date1.getMonth();
            var year2 = date2.getFullYear();
            var month2 = date2.getMonth();
            return (year2 - year1) * 12 + month2 - month1;
        };

        /**
         * 获取两个数的乘积
         * @param arg1
         * @param arg2
         * @returns {number}
         */
        this.accMul = function (arg1, arg2) {
            var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
            try {
                m += s1.split(".")[1].length
            } catch (e) {
            }
            try {
                m += s2.split(".")[1].length
            } catch (e) {
            }
            return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m)
        };

        /**
         * 获取多个数的和
         * @returns {number}
         */
        this.accAdd = function () {
            var argLength = arguments.length;
            var maxPrecision = 0;
            for (var i = 0; i < argLength; i++) {
                var precision = this.getPrecision(arguments[i]);
                if (precision > maxPrecision) {
                    maxPrecision = precision;
                }
            }
            var m = Math.pow(10, maxPrecision);
            var sum = 0;
            for (var i = 0; i < argLength; i++) {
                sum += arguments[i] * m;
            }
            return sum / m
        };

        /**
         * 获取数精度
         * @param value
         * @returns {number}
         */
        this.getPrecision = function (value) {
            var precision = 0;
            try {
                precision = value.toString().split(".")[1].length
            } catch (e) {
            }
            return precision;
        };

        /**
         * 格式化时间
         *
         * @param date 待格式化时间
         * @param fmt 时间格式
         * @return 指定格式的时间
         * @constructor
         *
         * 对Date的扩展，将 Date 转化为指定格式的String
         * 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
         * 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
         * 例子：
         * (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
         * (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
         */

        this.getFormatDate = function(date, fmt) {
            return new Date(date).Format(fmt);
        };
        Date.prototype.Format = function(fmt){
            if (fmt == null) {
                fmt = "yyyy-MM-dd";
            }
            var o = {
                "M+" : this.getMonth()+1,                 //月份
                "d+" : this.getDate(),                    //日
                "h+" : this.getHours(),                   //小时
                "m+" : this.getMinutes(),                 //分
                "s+" : this.getSeconds(),                 //秒
                "q+" : Math.floor((this.getMonth()+3)/3), //季度
                "S"  : this.getMilliseconds()             //毫秒
            };
            if(/(y+)/.test(fmt))
                fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
            for(var k in o)
                if(new RegExp("("+ k +")").test(fmt))
                    fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
            return fmt;
        };

        /**
         * 快速排序，按某个属性，或按“获取排序依据的函数”，来排序.
         * @method soryBy
         * @static
         * @param {array} arr 待处理数组
         * @param {string|function} prop 排序依据属性，获取
         * @param {boolean} desc 降序
         * @return {array} 返回排序后的新数组
         */
        this.sortBy =function (arr, prop, desc){
            var props = [],
                ret = [],
                i = 0,
                len = arr.length;
            if (typeof prop == 'string') {
                for (; i < len; i++) {
                    var oI = arr[i];
                    (props[i] = new String(oI && oI[prop] || ''))._obj = oI;
                }
            } else if (typeof prop == 'function') {
                for (; i < len; i++) {
                    var oI = arr[i];
                    (props[i] = new String(oI && prop(oI) || ''))._obj = oI;
                }
            } else {
                throw '参数类型错误';
            }
            props.sort(function(a, b) {
                return a - b;
            });
            for (i = 0; i < len; i++) {
                ret[i] = props[i]._obj;
            }
            if (desc) ret.reverse();
            return ret;
        };

        this.getCategoryItems = function(categoryValue, callback){
            $http.get('../categoryItem/getCategoryItems.do?categoryValue=' + categoryValue).success(function(data){
                var categoryItems = [];
                angular.forEach(data, function(item){
                    var categoryItem = {};
                    categoryItem.itemName = item.itemName;
                    categoryItem.itemValue = item.itemValue;
                    categoryItems.push(categoryItem);
                });
                callback && callback(categoryItems);
            }).error(function(data){

            });
        };
    })
});