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

package org.springframework.cloud.function.stream.app.source;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.function.compiler.SupplierCompiler;
import org.springframework.cloud.function.compiler.proxy.ByteCodeLoadingSupplier;
import org.springframework.cloud.function.compiler.proxy.LambdaCompilingSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import reactor.core.publisher.Flux;

/**
 * @author Mark Fisher
 */
@Configuration
@EnableConfigurationProperties(SupplierSourceProperties.class)
public class SupplierSourceConfiguration {

	@ConditionalOnProperty("lambda")
	static class LambdaConfiguration {

		@Autowired
		public SupplierSourceProperties properties;

		@Bean
		public SupplierCompiler<Flux<?>> compiler() {
			return new SupplierCompiler<>();
		}

		@Bean
		public Supplier<Flux<?>> supplier(SupplierCompiler<Flux<?>> compiler) {
			String lambda = properties.getLambda();
			Resource resource = new ByteArrayResource(lambda.getBytes());
			return new LambdaCompilingSupplier<>(resource, compiler);
		}
	}

	@ConditionalOnProperty("bytecode")
	static class ByteCodeConfiguration {

		@Autowired
		public SupplierSourceProperties properties;

		@Bean
		public Supplier<Flux<?>> supplier() {
			Resource bytecodeResource = properties.getBytecode();
			return new ByteCodeLoadingSupplier<>(bytecodeResource);
		}
	}
}
