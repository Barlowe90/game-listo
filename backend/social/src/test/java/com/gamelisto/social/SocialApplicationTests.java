package com.gamelisto.social;

import com.gamelisto.social.config.TestMessagingConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestMessagingConfig.class)
class SocialApplicationTests {

  @Test
  void contextLoads() {}
}



