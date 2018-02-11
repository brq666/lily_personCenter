// JavaScript Document
var PORT = 3001;

var http = require('http');
var url = require('url');
var fs = require('fs');
var mine = require('./nodeServer/mine').types;
var path = require('path');
var util = require("./nodeServer/util.js");
var mockRoute = require('./nodeserver/route.js');

var server = http.createServer(function (request, response) {
	var pathname = url.parse(request.url).pathname;
	var realPath = pathname.substring(1);
	var reqParams = url.parse(request.url, true).query;

	var ext = path.extname(realPath);

	if (ext != "") {
		ext = ext ? ext.slice(1) : 'unknown';
		fs.exists(realPath, function (exists) {
			if (!exists) {
				util.fRestApi(response, "This request URL " + pathname + " was not found on this server.", 404, 'text/plain');
			} else {
				fs.readFile(realPath, "binary", function (err, file) {
					if (err) {
						util.fRestApi(response, err, 500, 'text/plain');
					} else {
						var contentType = mine[ext] || "text/plain";
						//.fRestApi(response,file,200,contentType);
						response.writeHead(200, {
							'Content-Type': contentType
						});
						response.write(file, "binary");
						response.end();
					}
				});
			}
		});
	} else {
        console.log('pathname', pathname);
        mockRoute(request,response);
	}
});
server.listen(PORT);
console.log("Server runing at port: " + PORT + ".");