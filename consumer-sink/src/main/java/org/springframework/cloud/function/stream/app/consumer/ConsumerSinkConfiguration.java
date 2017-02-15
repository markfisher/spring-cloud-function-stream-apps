/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.cloud.function.stream.app.consumer;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.function.compiler.ConsumerCompiler;
import org.springframework.cloud.function.compiler.proxy.ByteCodeLoadingConsumer;
import org.springframework.cloud.function.compiler.proxy.LambdaCompilingConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

/**
 * @author Mark Fisher
 */
@Configuration
@EnableConfigurationProperties(ConsumerSinkProperties.class)
public class ConsumerSinkConfiguration {

	@ConditionalOnProperty("lambda")
	static class LambdaConfiguration {

		@Autowired
		public ConsumerSinkProperties properties;

		@Bean
		public ConsumerCompiler<?> compiler() {
			return new ConsumerCompiler<>();
		}

		@Bean
		public Consumer<?> consumer(ConsumerCompiler<?> compiler) {
			String lambda = properties.getLambda();
			Resource resource = new ByteArrayResource(lambda.getBytes());
			return new LambdaCompilingConsumer<>(resource, compiler);
		}
	}

	@ConditionalOnProperty("bytecode")
	static class ByteCodeConfiguration {

		@Autowired
		public ConsumerSinkProperties properties;

		@Bean
		public Consumer<?> consumer() {
			Resource bytecodeResource = properties.getBytecode();
			return new ByteCodeLoadingConsumer<>(bytecodeResource);
		}
	}
}
