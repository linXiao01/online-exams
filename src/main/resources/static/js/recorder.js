(function(exports){var Util={init:function(){navigator.getUserMedia=navigator.getUserMedia||navigator.webkitGetUserMedia||navigator.mozGetUserMedia||navigator.msGetUserMedia;window.AudioContext=window.AudioContext||window.webkitAudioContext},log:function(){console.log.apply(console,arguments)}};var Recorder=function(config){var _this=this;config=config||{};config.sampleRate=config.sampleRate||44100;config.bitRate=config.bitRate||128;Util.init();if(navigator.getUserMedia){navigator.getUserMedia({audio:true},function(stream){var context=new AudioContext(),microphone=context.createMediaStreamSource(stream),processor=context.createScriptProcessor(16384,1,1),successCallback,errorCallback;config.sampleRate=context.sampleRate;processor.onaudioprocess=function(event){var array=event.inputBuffer.getChannelData(0);realTimeWorker.postMessage({cmd:'encode',buf:array})};var realTimeWorker=new Worker('/js/worker.js');realTimeWorker.onmessage=function(e){switch(e.data.cmd){case'init':Util.log('初始化成功');if(config.success){config.success()}break;case'end':if(successCallback){var blob=new Blob(e.data.buf,{type:'audio/mp3'});successCallback(blob);Util.log('MP3大小：'+blob.size+'%cB','color:#0000EE')}break;case'error':Util.log('错误信息：'+e.data.error);if(errorCallback){errorCallback(e.data.error)}break;default:Util.log('未知信息：'+e.data)}};_this.start=function(){if(processor&&microphone){microphone.connect(processor);processor.connect(context.destination);Util.log('开始录音')}};_this.stop=function(){if(processor&&microphone){microphone.disconnect();processor.disconnect();Util.log('录音结束')}};_this.getBlob=function(onSuccess,onError){successCallback=onSuccess;errorCallback=onError;realTimeWorker.postMessage({cmd:'finish'})};realTimeWorker.postMessage({cmd:'init',config:{sampleRate:config.sampleRate,bitRate:config.bitRate}})},function(error){var msg;switch(error.code||error.name){case'PermissionDeniedError':case'PERMISSION_DENIED':case'NotAllowedError':msg='用户拒绝访问麦克风';break;case'NOT_SUPPORTED_ERROR':case'NotSupportedError':msg='浏览器不支持麦克风';break;case'MANDATORY_UNSATISFIED_ERROR':case'MandatoryUnsatisfiedError':msg='找不到麦克风设备';break;default:msg='无法打开麦克风，异常信息:'+(error.code||error.name);break}Util.log(msg);if(config.error){config.error(msg)}})}else{Util.log('当前浏览器不支持录音功能');if(config.fix){config.fix('当前浏览器不支持录音功能')}}};exports.Recorder=Recorder})(window);