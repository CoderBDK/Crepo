package com.lazy.crepo_example.core.api

import com.lazy.crepo.RepoInject
import com.lazy.crepo.Repository
import com.lazy.crepo_example.core.model.Res

@Repository
interface ApiService {
    suspend fun data(): Res
}