package com.me.harris.simdjson.desc

import kotlinx.serialization.Serializable


@Serializable
data class TimeLine(val statuses: List<Status>,val search_metadata:SearchMeta)
@Serializable
data class Status(val created_at: String,val text:String,val source:String)

@Serializable
data class SearchMeta(val count: Int)
