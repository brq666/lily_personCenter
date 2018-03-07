/*工具类 nodeserver 专用
    add by lym
*/
var fs = require('fs');
var util={
    _logfile:"./nodeserver/log.txt",
    /*
        resp:response对象
        content：打印内容
        status: http状态码 有200 500 404 等
        contenttype：内容类型 有：application/json、text/plain
     */
    fRestApi : function(resp,content,status,contenttype)
    {
        var _status=200,_contenttype="application/json";
        if(typeof(status) != "undefined")
        {
            _status = status;
        }
        if(typeof(contenttype) != "undefined")
        {
            _contenttype = contenttype;
        }
        resp.writeHead(_status, {
            'Content-Type': _contenttype
        });

        resp.write(content);
        resp.end();
    },
    fWriteLog : function(str)/*写文件日志*/
    {
        fs.appendFile(this._logfile, str+" \r\n", function(err){

            if(err)
            {
                console.log("fail " + err);
            }
            else
            {
                //console.log("写入文件ok");
            }
        });
    },
    fGetRoutePath : function(url)/*获取url里面第一级目录 用来判断是restapi还是nativeapi 等等*/
    {
        var ret = "";
        try
        {
            var pathArray = url.split("/");
            ret = pathArray[1];
        }
        catch(err)
        {
            console.log(err);
        }
        return ret;
    }

}
module.exports = util;