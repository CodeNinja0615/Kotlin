package com.example.weatherapp.model

import java.io.Serializable

// Main response data class
data class WeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val rain: Rain?,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
): Serializable

// Coord data class for coordinates
data class Coord(
    val lon: Double,
    val lat: Double
): Serializable

// Weather data class for weather conditions
data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
): Serializable

// Main data class for main weather parameters
data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Double,
    val humidity: Int,
    val sea_level: Double,
    val grnd_level: Double
): Serializable

// Wind data class for wind parameters
data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
): Serializable

// Rain data class for rain volume
data class Rain(
    val `1h`: Double
): Serializable

// Clouds data class for cloud coverage
data class Clouds(
    val all: Int
): Serializable

// Sys data class for system information
data class Sys(
    val type: Int,
    val id: Int,
    val message: Double,
    val country: String ,
    val sunrise: Long,
    val sunset: Long
): Serializable
