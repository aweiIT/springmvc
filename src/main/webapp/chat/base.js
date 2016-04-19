$.extend({// 获取http请求参数
	getUrlVars : function() {
		var vars = [], hash;
		var hashes = window.location.href.slice(
				window.location.href.indexOf('?') + 1).split('&');
		for ( var i = 0; i < hashes.length; i++) {
			hash = hashes[i].split('=');
			vars.push(hash[0]);
			vars[hash[0]] = hash[1];
		}
		return vars;
	},
	getUrlVar : function(name) {
		return $.getUrlVars()[name];
	}
});
function timeStamp2String(time) {// 时间转换
	var datetime = new Date();
	if (time != null)
		datetime.setTime(time);
	var year = datetime.getFullYear();
	var month = datetime.getMonth() + 1 < 10 ? "0" + (datetime.getMonth() + 1)
			: datetime.getMonth() + 1;
	var date = datetime.getDate() < 10 ? "0" + datetime.getDate() : datetime
			.getDate();
	var hour = datetime.getHours() < 10 ? "0" + datetime.getHours() : datetime
			.getHours();
	var minute = datetime.getMinutes() < 10 ? "0" + datetime.getMinutes()
			: datetime.getMinutes();
	var second = datetime.getSeconds() < 10 ? "0" + datetime.getSeconds()
			: datetime.getSeconds();
	return year + "-" + month + "-" + date + " " + hour + ":" + minute + ":"
			+ second;
}
function getUlr(param) {
	var str = "?";
	$.each(param, function(name, value) {
		if (str != "?")
			name = "&" + name;
		str = str + name + "=" + value;
	});
	return str;
}

function addTouchEvent(divObj, title, url, type) {// 给dom添加滑动事件，左滑右滑分别跳到不同的页面
	var touchObj = {};
	var timerId;
	var number = 1;
	divObj.addEventListener("touchstart", function(event) {
		// event.preventDefault();
		var touch = event.targetTouches[0];
		// 把元素放在手指所在的位置
		touchObj.startx = touch.pageX;
		touchObj.starty = touch.pageY;
	}, false);
	divObj.addEventListener("touchmove", function(event) {
		var touch = event.changedTouches[0];
		touchObj.sub_x = touch.pageX - touchObj.startx;
		touchObj.sub_y = touch.pageY - touchObj.starty;
		touchObj.nowX = Math.abs(touchObj.sub_x);
		touchObj.nowY = Math.abs(touchObj.sub_y);
		if (type == "left") {
			if (touchObj.nowX > touchObj.nowY * 4 && touchObj.sub_x > 40) {
				event.preventDefault();
			}
		}

		if (type == "right") {
			if (touchObj.nowX > touchObj.nowY * 4 && touchObj.sub_x < -40) {
				event.preventDefault();
			}
		}

	}, false);
	divObj.addEventListener("touchend", function(event) {
		var touch = event.changedTouches[0];
		touchObj.sub_x = touch.pageX - touchObj.startx;
		touchObj.sub_y = touch.pageY - touchObj.starty;
		touchObj.nowX = Math.abs(touchObj.sub_x);
		touchObj.nowY = Math.abs(touchObj.sub_y);

		// alert(type + "_" + touch.pageX + "_" + touch.pageY + "_"
		// + touchObj.startx + "_" + touchObj.starty + "_"
		// + touchObj.sub_x);

		if (type == "left") {
			if (touchObj.nowX > touchObj.nowY * 4 && touchObj.sub_x > 40) {
				event.preventDefault();
				openRightRelate(title, url, "leftMove");
			}
		}

		if (type == "right") {
			if (touchObj.nowX > touchObj.nowY * 4 && touchObj.sub_x < -40) {
				event.preventDefault();
				openRightRelate(title, url, "")
			}
		}
	}, false);
}

var subText = function(dom, num) {

	var number = 52;
	if (num != null)
		number = num;
	var nodes = dom[0].childNodes;
	var key = "";
	for ( var i = 0; i < nodes.length; i++) {
		if (nodes[i].nodeName != "#text")
			key = "innerHTML";
		else
			key = "nodeValue";

		if (number > 0)
			if (nodes[i][key].length <= number) {
				number = number - nodes[i][key].length;
			} else {
				if (key == "innerHTML")
					nodes[i][key] = nodes[i][key].substring(0, number)
							+ "<span style='color:black'>...</span>";
				else
					nodes[i][key] = nodes[i][key].substring(0, number) + "...";
				number = 0;
			}
		else {
			dom[0].removeChild(nodes[i]);
			i--;
		}
	}
	if (number > 0) {
		return true;
	}
	return false;
}
/** ajax异步提交数据 */
function ajaxPost(param, action, successMethod) {
	$.ajax({
		data : param,
		type : "post",
		url : action,
		dataType : "json",/* 这句可用可不用，没有影响 */
		contentType : "application/json; charset=UTF-8",
		success : successMethod,
		error : function(XMLHttpRequest, textStatus, errorThrown) {
		}
	});
}