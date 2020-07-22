/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example;

import org.junit.Test;

import org.example.RegisterService;

public class ClientTest {

	@Test
	public void testFabCar() throws Exception {
		EnrollAdmin.main(null);
		RegisterUser.main(null);
		RegisterService.main(null);
		ClientApp.main(null);
	}
}