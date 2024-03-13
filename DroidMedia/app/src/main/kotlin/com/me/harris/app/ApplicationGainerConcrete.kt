package com.me.harris.app

import android.app.Application
import com.me.harris.droidmedia.BaseApplication
import com.me.harris.serviceapi.applicationGainer.IApplicationLike

class ApplicationGainerConcrete(override val application: Application = BaseApplication.instance): IApplicationLike  {

}
