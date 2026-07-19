package com.usher.url.shortening.application

class InvalidOriginalUrlException : RuntimeException("Original URL must be a valid http or https URL")
