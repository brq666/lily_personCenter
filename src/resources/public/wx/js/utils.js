/**
 * Created by bloodyears on 16/8/14.
 */


Date.prototype.format = function(fmt){
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

var jsonToParams = function(obj) {
  var params = "";
  for(var item in obj) {
    var value = obj[item];
    if(value == null || value == "" || typeof(value)== "function") {
      continue;
    }
    if(params!="") {
      params += "&"
    }
    params += item+"="+value;
  }
  return params;
};

var goWithParams = function (to, params) {
  var search = "";
  var indexSearch = window.location.hash.indexOf('?');
  if(indexSearch>=0) {
    search = window.location.hash.substr(indexSearch+1);
  }
  if(params) {
    for(var key in params) {
      if(search.length>0) {
        search += "&";
      }
      search += key+"="+params[key];
    }
  }
  if(search) {
    window.location.hash = '#/'+to+"?"+search;
  } else {
    window.location.hash = '#/'+to;
  }
};

//获取url中的参数
var getURLParams = function(url){
  if (url.indexOf("?") == -1) {
    return {};
  }
  var params = {};
  var str = url.split("?")[1];  //通过?得到一个数组,取?后面的参数
  var items = [str];
  if (url.indexOf("&") != -1) {
    items = str.split("&"); //分割成数组
  }
  var arr,name,value;
  for(var i=0; i<items.length; i++){
    arr = items[i].split("=");
    name = arr[0];
    value = arr[1];
    params[name] = value;
  }
  return params;
};

//获取更深或更浅的颜色
var changeSaturation = function(color, change) {
  var rgb = hexToRgb(color);
  var deltaR = change*(255-rgb[0])/(255-(rgb[0]+rgb[1]+rgb[2])/3);
  var deltaG = change*(255-rgb[1])/(255-(rgb[0]+rgb[1]+rgb[2])/3);
  var deltaB = change*(255-rgb[2])/(255-(rgb[0]+rgb[1]+rgb[2])/3);
  var newRgb = [Math.min(Math.max(Math.round(rgb[0]+deltaR), 0), 255),
    Math.min(Math.max(Math.round(rgb[1]+deltaG), 0), 255),
    Math.min(Math.max(Math.round(rgb[2]+deltaB), 0), 255)];
  return rgbToHex(newRgb);
};

// 处理rgb颜色转换到十六进制颜色,"#yyyyyy"=>[xxx,xxx,xxx]
// 能处理 #axbycz 或 #abc 形式
var hexToRgb = function(hex) {
  var rgb = [];
  try {
    hex = hex.replace(/#/,"");
    // 处理 "#abc" 成 "#aabbcc"
    if (hex.length == 3) {
      var tmp = [];
      for (var i = 0; i < 3; i++) {
        tmp.push(hex.charAt(i) + hex.charAt(i));
      }
      hex = tmp.join("");
    }
    for (var j = 0; j < 3; j++) {
      var color = "0x" + hex.substr(j*2, 2);
      rgb.push(parseInt(Number(color)));
    }
  }
  catch(e) {
    rgb = [0, 0, 0];
  }
  return rgb;
};

// 处理rgb颜色转换到十六进制颜色,[xxx,xxx,xxx]=>"#yyyyyy"
var rgbToHex = function(rgb) {
  var hex = "#";
  for (var i = 0; i < 3; i++) {
    // 'Number.toString(16)' 是JS默认能实现转换成16进制数的方法.
    // 如果结果是一位数，就在前面补零。例如： A变成0A
    hex += ("0" + Number(rgb[i]).toString(16)).slice(-2);
  }
  return hex;
};

var distanceBetween = function (point1, point2) {
  return Math.sqrt(Math.pow(point1.x-point2.x, 2)+Math.pow(point1.y-point2.y, 2));
};
