package com.example.smack.Utilities

const val BASE_URL = "https://virunee-smack-chat-api.herokuapp.com/v1/"
const val SOCKET_URL = "https://virunee-smack-chat-api.herokuapp.com/"

const val URL_REGISTER = "${BASE_URL}account/register"
const val URL_LOGIN = "${BASE_URL}account/login"
const val URL_CREATE_USER = "${BASE_URL}user/add"
const val URL_GET_USER = "${BASE_URL}user/byEmail/"

// Broadcast constants
const val BROADCAST_USER_DATA_CHANGE = "BROADCAST_USER_DATA_CHANGE"