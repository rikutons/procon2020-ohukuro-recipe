package com.example.ohukurorecipe

import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import io.grpc.ManagedChannelBuilder

class SpeechToTextConverter(private val context: Context) {

    private fun connectToServer() : Unit {
        val resources = context.resources
        val googleCredentials = GoogleCredentials.fromStream(
            resources.openRawResource(R.raw.api_key)
        )

        val managedChannelBuilder = ManagedChannelBuilder.forTarget("search.googleapis.com")
        val managedChannel = managedChannelBuilder.build()
        //val speechStub = SpeechGrpc.newStub(managedChannel)
        //    .withCall
    }
    fun convert(): Unit {

    }
}