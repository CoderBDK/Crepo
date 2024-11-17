# Crepo

Crepo is a lightweight Kotlin library designed to simplify repository generation for Retrofit based Android projects using annotation processing.

## Features

- Automatically generates repositories for your data sources.
- Supports Hilt injection.
- Simplifies managing API and data handling logic.

---

## Installation

### Step 1: Add JitPack to your project repositories

Ensure that your project’s `settings.gradle.kts` file includes the JitPack repository:
```
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```

### Step 2: Add Crepo to your dependencies

In your `app/build.gradle.kts`, add the following dependency:
```
dependencies {
	        implementation("com.github.lazy-pr0grammer.crepo:crepo:1.0.0")
          ksp("com.github.lazy-pr0grammer.crepo:crepo-processor:1.0.0")
	}
```


---

## Usage

### Annotating Your Class

Use the `@Repository` annotation on any class you want to generate a repository for:
```
@Repository
interface ApiService {
    @POST("api/post")
    suspend fun data(@Body data: Req): Res
}
```

### Optional: Using `@RepoInject`

You can also use `@RepoInject` to use it with Hilt:
```
@Repository
@RepoInject
interface ApiService {
    @POST("api/post")
    suspend fun data(@Body data: Req): Res
}
```

**Generated repository:**

It just adds an `@Inject` annotation before `constructor()` if you use `@RepoInject` to inject `ApiService` via Hilt
```
class ApiServiceRepository constructor(private val api: ApiService) {

    fun data(data: com.lazy.crepo_example.core.api.Req): Flow<DataState<com.lazy.crepo_example.core.model.Res>> = flow {
        emit(DataState.Loading)
        try {
            emit(DataState.Success(api.data(data)))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

}
```

---

## Contributing

Feel free to submit issues or contribute to the project on [GitHub](https://github.com/lazy-pr0grammer/crepo).

---

## License

This project is licensed under the MIT License. See the LICENSE file for details.

