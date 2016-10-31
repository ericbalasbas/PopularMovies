# Popular Movies Project, Stage 1


## Installation

Add the following code in the app\build.gradle file, in the android section:

`buildTypes.each {
        it.buildConfigField 'String', 'THE_MOVIE_DB_API_KEY', MyMovieDbApiKey
    }`


Place the Movie DB API key in the global gradle.properties file:

`MyMovieDbApiKey="<API_KEY>"`


## Copyright

Copyright (c) 2016. Eric Balasbas
