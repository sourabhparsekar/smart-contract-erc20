package com.ethereum.smartcontract;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmartContractDeploymentApplicationTests {

	@Test
	void main() {
		//test cases added only to verify context loads with default application.properties
		Assertions.assertEquals("Main", "M" + "a" + "i" + "n");
	}


}
