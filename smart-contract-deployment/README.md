- added solc compiler in resources folder
- added web3j plugin in build.gradle
- added solidity configuration in build.gradle
- added web3j dependency in build.gradle

https://github.com/web3j/web3j-gradle-plugin
After applying the plugin, the base directory for generated code (by default $buildDir/generated/sources/web3j) will contain a directory for each source set (by default main and test) containing the smart contract wrappers Java classes.

By default, all .sol files in $projectDir/src/main/solidity will be processed by the plugin. To specify and add different source sets, use the sourceSets DSL:

https://github.com/web3j/solidity-gradle-plugin


gradle 7.x does not work with solidity plugin. so downgraded version