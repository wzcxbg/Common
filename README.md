### How to use?

#### 1. Edit your project's `settings.gradle` file, add follow line like this

```
dependencyResolutionManagement {
        ......
        maven {
            url = uri("https://maven.pkg.github.com/wzcxbg/common")
            credentials {
                username = "wzcxbg"
                password = "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"
            }
        }
    }
}
rootProject.name = "Common"
include ':app'
```

#### 2. Edit your module's `build.gradle` file, add dependencie in the dependencies node

```
implementation 'com.sliver:common:1.0'
```

#### 3. Click 'sync' Button and enjoy it
