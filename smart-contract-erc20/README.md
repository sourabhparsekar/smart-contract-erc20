# ERC20 basic sample hardhat project

This project demonstrates an basic ERC-20 Token use case, integrating other tools commonly used alongside Hardhat in the ecosystem.

The project comes with a contract, a test for that contract, a sample script that deploys that contract, and an example of a task implementation, which simply lists the available accounts. It also comes with a variety of other tools, preconfigured to work with the project code.

The code has been sourced from [UNDERSTAND THE ERC-20 TOKEN SMART CONTRACT](https://ethereum.org/en/developers/tutorials/understand-the-erc-20-token-smart-contract/) tutorial. 

Following tasks are pre-configured 

```shell
npx hardhat accounts
npx hardhat compile
npx hardhat clean
npx hardhat test
npx hardhat node
npx hardhat help
REPORT_GAS=true npx hardhat test
npx hardhat coverage
npx hardhat run scripts/deploy.js
node scripts/deploy.js
npx eslint '**/*.js'
npx eslint '**/*.js' --fix
npx prettier '**/*.{json,sol,md}' --check
npx prettier '**/*.{json,sol,md}' --write
npx solhint 'contracts/**/*.sol'
npx solhint 'contracts/**/*.sol' --fix
```

# Smart Contract Build & Test

yarn needs to be installed to use the yarn commands to build the Smart Contract. However, the above hardhat tasks can also be used to compile and test the Smart Contract. 

Below are yarn commands to be executed sequentially to build the Smart Contract

```shell
yarn install
yarn compile
yarn test
```

# Etherscan verification

For Etherscan verification, you first need to deploy a contract to an Ethereum network that's supported by Etherscan, such as Ropsten.

In this project, copy the .env.example file to a file named .env, and then edit it to fill in the details. Enter your Etherscan API key, your Ropsten node URL (eg from Alchemy), and the private key of the account which will send the deployment transaction. With a valid .env file in place, first deploy your contract:

```shell
yarn deploy:ropsten
```

Then, copy the deployment address and paste it in to replace `DEPLOYED_CONTRACT_ADDRESS` in this command:

```shell
npx hardhat verify --network ropsten DEPLOYED_CONTRACT_ADDRESS 10000
```
