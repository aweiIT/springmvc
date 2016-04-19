var webSocket = null;
var sender;
var tryTime = 0;
var imgNum;
var height = 0;
var msgList = {};
var sendID;
// 聊天记录
function write(type, imgID, msg) {
	if (type == "receive") {
		var text_div = $("<div/>").addClass("receive_div").appendTo(
				$(".content"));
		var img = $("<div/>").addClass("head_img").css({
			"background-image" : "url('img/QQ/" + imgID + ".bmp')",
			"float" : "left"
		}).appendTo(text_div);
		var div = $("<div/>").addClass("content_text").css({
			"float" : "left",
			"background-color" : "#CDD7E2"
		}).text(msg).appendTo(text_div);
		$("<div/>").addClass("clear").appendTo(text_div);
		height += text_div.height();
	} else {
		var text_div = $("<div/>").addClass("text_div").appendTo($(".content"));
		var img = $("<div/>").addClass("head_img").css("background-image",
				"url('img/QQ/" + imgNum + ".bmp')").appendTo(text_div);
		var div = $("<div/>").addClass("content_text").css({}).text(msg)
				.appendTo(text_div);
		$("<div/>").addClass("clear").appendTo(text_div);
		height += text_div.height();
	}
}
function chat_record(sendId) {
	sendID = sendId;
	var array = msgList[sendId];
	if (array != null)
		for ( var i = 0; i < array.length; i++) {

			write(array[i].type, array[i].imgID, array[i].msg);

		}
	$(".content").scrollTop(height);
}
/*
 * var msgObj = msgList[sendId]; if (msgObj == null) { msgObj = new Array(); }
 * 
 * for ( var i = 0; i < obj.length; i++) { var text_div = $("<div/>").addClass("receive_div").appendTo(
 * $(".content")); var img = $("<div/>").addClass("head_img").css( {
 * "background-image" : "url('img/chat_head/" + obj[i].imgID + ".png')", "float" :
 * "left" }).appendTo(text_div); var div = $("<div/>").addClass("content_text").css({
 * "float" : "left", "background-color" : "#CDD7E2"
 * }).text(obj[i].msg).appendTo(text_div); $("<div/>").addClass("clear").appendTo(text_div);
 * height += text_div.height(); } $(".content").scrollTop(height);
 */

function send(sendId) {
	var msgArray = msgList[sendId];
	if (msgArray == null) {
		msgArray = new Array();
	}
	var msgObj = {};
	msgObj.type = "send";
	msgObj.imgID = imgNum;
	msgObj.msg = $(".input").text();
	msgArray.push(msgObj);
	msgList[sendId] = msgArray;

	var data = {
		sendId : sendId,
		imgID : imgNum,
		msg : $(".input").text()
	};
	write("send", imgNum, $(".input").text());
	$(".content").scrollTop(height);
	webSocket.send(JSON.stringify(data));
	$(".input").text("");
}

function calls() {
	document.getElementById("audio").play();

	// autoPlayAudio1();

	navigator.vibrate = navigator.vibrate || navigator.webkitVibrate
			|| navigator.mozVibrate || navigator.msVibrate;
	if (navigator.vibrate)
		navigator.vibrate([ 200, 200, 100, 100 ]);
}
var isInit = false;
function initSocket(ID) {
	if (!ID)
		return;
	var userCode = "123";
	webSocket = new WebSocket("ws://localhost:8081/springmvc/websocket.ws/" + ID
			+ "/" + userCode);
	// 收到服务端消息
	webSocket.onmessage = function(event) {
		var datas = event.data;
		var obj = JSON.parse(datas);
		calls();// 接收消息提示音和震动(仅android有)
		if (obj.type == "add") {
			newUser(obj);
			return;
		}

		$.each(obj, function(name, value) {
			var msgArray = msgList[name];
			if (msgArray == null) {
				msgArray = new Array();
			}
			for ( var i = 0; i < value.length; i++) {
				var msgObj = {};
				msgObj.type = "receive";
				msgObj.imgID = value[i].imgID;
				msgObj.msg = value[i].msg;
				msgArray.push(msgObj);
				if (name == sendID) {
					write("receive", value[i].imgID, value[i].msg);
				} else {
					// 外边标未读数字
					var num = $("#" + name).attr("num");
					var notRead;
					if (num == null) {
						num = 1;
						$("<div/>").addClass("notRead").text(num).insertAfter(
								$("#" + name).find(".context"));
					} else {
						num = parseInt(num) + 1;
						$("#" + name).find(".notRead").text(num);
					}
					$("#" + name).attr("num", num);
				}
			}
			msgList[name] = msgArray;
			$(".content").scrollTop(height);
		});
	};
	// 异常
	webSocket.onerror = function(event) {
		console.log("onerror" + event);
	};
	// 建立连接
	webSocket.onopen = function(event) {
		isInit = true;
		console.log("onopen" + event);
	};
	// 断线重连
	webSocket.onclose = function() {
		// 重试10次，每次之间间隔10秒
		if (tryTime < 30) {
			setTimeout(function() {
				webSocket = null;
				tryTime++;
				initSocket();
			}, 2000);
		} else {
			tryTime = 0;
		}
	};
}
function newUser(obj) {
	var userList = new Array();
	userList.push(obj);
	$(".newUser").removeClass("newUser");
	$("#item").tmpl({
		data : userList
	}).insertAfter($(".onLine").children(".item:eq(0)"));
	$(".newUser").addClass("light");
}
function itemClickEvent(div) {
	var sendId = $(div).attr("id");
	var name = $(div).find(".name").text();
	$(div).children(".notRead").remove();
	$(div).removeAttr("num", "0");
	$(".onLine").hide();
	$(".chatMsg").load("chat.html", function() {
		$(".chatName").text("与" + name + "聊天中").css({
			"text-align" : "center",
			"margin-left" : "50px",
			"color" : "blue"
		});

		chat_record(sendId);
		$(".btn").on("click", function(sendId) {
			return function() {
				if (isInit) {
					send(sendId);
				}
			}
		}(sendId));
		$(".back").click(function() {
			sendID = "";
			$(".onLine").show();
			$(".chat").remove();
		});
	});
}
/*
 * 检查cookie
 */
function getCookie() {
	var userName = "xzwei_chat";
	var cookie = document.cookie;
	var c_start = document.cookie.indexOf(userName + "=");
	if (c_start != -1) {
		c_start = c_start + userName.length + 1;
		c_end = document.cookie.indexOf(";", c_start);
		if (c_end == -1)
			c_end = document.cookie.length;
		var str = document.cookie.substring(c_start, c_end);
		return str;
	} else {
		return null;
	}
}
function setCookie(id) {
	var userName = "xzwei_chat";
	var exdate = new Date();
	exdate.setDate(exdate.getDate() + 3600);
	document.cookie = userName + "=" + id + "; expires=" + exdate.toGMTString();
}
