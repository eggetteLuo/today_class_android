package com.eggetteluo.todayclass.di

import com.eggetteluo.todayclass.feature.upload.UploadViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {

    viewModelOf(::UploadViewModel)

}