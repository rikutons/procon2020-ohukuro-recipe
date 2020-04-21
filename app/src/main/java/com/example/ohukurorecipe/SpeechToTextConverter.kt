package com.example.ohukurorecipe

import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.speech.v1.RecognitionConfig
import com.google.cloud.speech.v1.SpeechGrpc
import com.google.cloud.speech.v1.StreamingRecognitionConfig
import com.google.cloud.speech.v1.StreamingRecognizeRequest
import com.google.protobuf.ByteString
import io.grpc.ManagedChannelBuilder
import io.grpc.auth.MoreCallCredentials
import io.grpc.stub.StreamObserver


class SpeechToTextConverter(private val context: Context) {
    private lateinit var speechStub : SpeechGrpc.SpeechStub
    private lateinit var observer: StreamObserver<StreamingRecognizeRequest>
    fun connectToServer() : Unit {
        val resources = context.resources
        val googleCredentials = GoogleCredentials.fromStream(
            resources.openRawResource(R.raw.api_key)
        )

        val managedChannelBuilder = ManagedChannelBuilder.forTarget("search.googleapis.com")
        val managedChannel = managedChannelBuilder.build()
        speechStub = SpeechGrpc.newStub(managedChannel)
            .withCallCredentials(MoreCallCredentials.from(googleCredentials))
    }

    fun startStreaming() : Unit {
        observer = speechStub.streamingRecognize(MyObserver())

        val config = StreamingRecognitionConfig.newBuilder()
            .setConfig(
                RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(context.resources.getInteger(R.integer.sample_rate_hertz))
                    .setLanguageCode(context.resources.getString(R.string.language_code))
                    .build()
            )
            .setSingleUtterance(true)
            .setInterimResults(true)
            .build()

        observer.onNext(
            StreamingRecognizeRequest.newBuilder()
                .setStreamingConfig(config)
                .build()
        )
    }
    fun convert(fragment: ByteString): Unit {
        observer.onNext(
            StreamingRecognizeRequest.newBuilder()
                .setAudioContent(fragment)
                .build()
        )
    }
    fun endStreaming(): Unit {
        observer.onCompleted()
    }
}