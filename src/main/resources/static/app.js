var stompClient = null;

function setConnected(connected) { // Connect/Disconnect button
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

var connected = false;

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        connected = true;
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/arrival', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/car", {}, JSON.stringify({'carOwner': "replace-with-username"}));  // todo: replace with username
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    connect();
    (function(){
        if (connected) {
            console.log("sending msg");
            sendName();
        }
        setTimeout(arguments.callee, 30000);

    })();
    $( "#send" ).click(function() { sendName(); });
});
