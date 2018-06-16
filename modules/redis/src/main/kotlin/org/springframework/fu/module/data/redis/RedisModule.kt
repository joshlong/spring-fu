/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.fu.module.data.redis

import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.core.io.ResourceLoader
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.fu.AbstractModule
import org.springframework.fu.ApplicationDsl
import org.springframework.fu.ref
import java.net.URI
import java.time.Duration


/**
 * @author Josh Long
 */
open class RedisModule(
		private val url: String,
		private val database: Int,
		private val init: RedisModule.() -> Unit) : AbstractModule() {

	private data class ConnectionInfo(
			val uri: URI,
			val useSsl: Boolean,
			val password: String? = null,
			val hostName: String = uri.host,
			val port: Int = uri.port
	)

	private fun parseUrl(url: String): ConnectionInfo {
		val uri = URI(url)
		val useSsl = url.startsWith("rediss://")
		val password = uri.userInfo?.let {
			val indx = it.indexOf(58.toChar())
			if (indx >= 0) {
				it.substring(indx + 1)
			}
			it
		}
		return ConnectionInfo(uri, useSsl, password)
	}

	private fun lettuceConnectionFactory(
			url: String,
			db: Int,
			commandTimeout: Duration = Duration.ofSeconds(60L),
			shutdownTimeout: Duration = Duration.ofMillis(100L)): LettuceConnectionFactory {

		val connectionInfo = this.parseUrl(url)

		val config: RedisStandaloneConfiguration = RedisStandaloneConfiguration()
				.let {
					it.hostName = connectionInfo.hostName
					it.port = connectionInfo.port
					it.password = RedisPassword.of(connectionInfo.password)
					it.database = db
					it
				}

		val lcc: LettuceClientConfiguration = LettuceClientConfiguration
				.builder()
				.let { lccb ->
					connectionInfo.useSsl.let { ssl ->
						if (ssl) {
							lccb.useSsl()
						}
					}
					lccb.shutdownTimeout(shutdownTimeout)
					lccb.commandTimeout(commandTimeout)
					lccb.build()
				}

		return LettuceConnectionFactory(config, lcc)
	}

	override lateinit var context: GenericApplicationContext

	override fun initialize(context: GenericApplicationContext) {

		this.context = context

		this.init()

		this.context.registerBean<RedisSerializationContext<Any, Any>> {

			val jdkSerializer = JdkSerializationRedisSerializer(ref<ResourceLoader>().classLoader)

			RedisSerializationContext
				.newSerializationContext<Any, Any>()
				.key(jdkSerializer)
				.value(jdkSerializer)
				.hashKey(jdkSerializer)
				.hashValue(jdkSerializer)
				.build()
		}

		this.context.registerBean {
			ReactiveRedisTemplate<Any, Any>(ref(), ref())
		}

		this.context.registerBean {
			StringRedisTemplate(ref())
		}

		this.context.registerBean { this.lettuceConnectionFactory(this.url, this.database) }

		super.initialize(context)
	}

}

fun ApplicationDsl.redis(
		url: String = "redis://localhost:6379",
		database: Int = 0,
		init: RedisModule.() -> Unit = {}): RedisModule {
	val redisModule = RedisModule(url, database, init)
	initializers.add(redisModule)
	return redisModule
}