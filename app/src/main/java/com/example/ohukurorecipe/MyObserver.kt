package com.example.ohukurorecipe

import com.google.cloud.speech.v1.StreamingRecognizeResponse
import io.grpc.stub.StreamObserver


class MyObserver : StreamObserver<StreamingRecognizeResponse> {
    override fun onNext(value: StreamingRecognizeResponse?) {
        if(value!!.hasError()){
            //Error handling
            return
        }
        if(
            value?.speechEventType ==
            StreamingRecognizeResponse.SpeechEventType.END_OF_SINGLE_UTTERANCE
        ) {
            //一区切りの認識の終了処理
            return
        }

        var newMsg = ""
        for (res in value.resultsList) {
            newMsg += res.getAlternatives(0).transcript
        }
        //newMsg...現在認識中の文字列
    }

    override fun onError(t: Throwable?) {
        //相手のonNextで例外が発生したときに呼ばれる模様。おそらくこのAPIで使用されることはない
    }

    override fun onCompleted() {
        //ストリームを閉じる処理
    }
}