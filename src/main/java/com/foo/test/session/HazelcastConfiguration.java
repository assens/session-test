package com.foo.test.session;

import static org.springframework.session.hazelcast.HazelcastIndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.hazelcast.PrincipalNameExtractor;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapAttributeConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.HazelcastInstanceFactory;

@Configuration
public class HazelcastConfiguration {

  @Bean
  public HazelcastInstance hazelcastInstance(Config config) {
    return HazelcastInstanceFactory.getOrCreateHazelcastInstance(config);
  }

  @Bean
  public Config hazelcastConfig() {
    final Config config = new Config();
    final NetworkConfig network = config.getNetworkConfig();
    final JoinConfig join = network.getJoin();
    final TcpIpConfig tcpIpConfig = join.getTcpIpConfig();
    final MulticastConfig multicastConfig = join.getMulticastConfig();
    config.setInstanceName("test");
    tcpIpConfig.setEnabled(true);
    multicastConfig.setEnabled(false);
    network.getInterfaces()
        .setEnabled(true)
        .setInterfaces(Collections.singletonList("127.0.0.1"));

    // Spring Session
    final MapAttributeConfig attributeConfig = new MapAttributeConfig()
        .setName(PRINCIPAL_NAME_ATTRIBUTE)
        .setExtractor(PrincipalNameExtractor.class.getName());

    config.getMapConfig("spring:session:sessions")
        .addMapAttributeConfig(attributeConfig)
        .addMapIndexConfig(new MapIndexConfig(PRINCIPAL_NAME_ATTRIBUTE, false));

    return config;
  }
}
