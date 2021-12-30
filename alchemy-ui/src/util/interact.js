require("dotenv").config();
const alchemyKey = process.env.REACT_APP_ALCHEMY_KEY;
const { createAlchemyWeb3 } = require("@alch/alchemy-web3");
const web3 = createAlchemyWeb3(alchemyKey);

const contractABI = require("../contract-abi.json");
const contractAddress = process.env.REACT_APP_CONTRACT_ADDRESS;

console.log('Alchemy Key - ' + alchemyKey)
console.log('Contract Address - ' + contractAddress)

export const smartContract = new web3.eth.Contract(
  contractABI,
  contractAddress
);

export const loadContractName = async () => {
  const message = await smartContract.methods.name().call();
  return message;
};

export const loadContractSymbol = async () => {
  const message = await smartContract.methods.symbol().call();
  return message;
};

export const loadContractTotalSupply = async () => {
  const message = await smartContract.methods.totalSupply().call();
  return message;
};

export const loadContractDecimals = async () => {
  const message = await smartContract.methods.decimals().call();
  return message;
};

export const connectWallet = async () => {
  if (window.ethereum) {
    try {
      const addressArray = await window.ethereum.request({
        method: "eth_requestAccounts",
      });
      const obj = {
        status: "👆🏽 Write a message in the text-field above.",
        address: addressArray[0],
      };
      return obj;
    } catch (err) {
      return {
        address: "",
        status: "😥 " + err.message,
      };
    }
  } else {
    return {
      address: "",
      status: (
        <span>
          <p>
            {" "}
            🦊{" "}
            <a target="_blank" rel="noreferrer" href={`https://metamask.io/download.html`}>
              You must install Metamask, a virtual Ethereum wallet, in your
              browser.
            </a>
          </p>
        </span>
      ),
    };
  }
};

export const getCurrentWalletConnected = async () => {
  if (window.ethereum) {
    try {
      const addressArray = await window.ethereum.request({
        method: "eth_accounts",
      });
      if (addressArray.length > 0) {
        return {
          address: addressArray[0],
          status: "👆🏽 Populate the Data and Click on Button to execute...",
        };
      } else {
        return {
          address: "",
          status: "🦊 Connect to Metamask using the top right button.",
        };
      }
    } catch (err) {
      return {
        address: "",
        status: "😥 " + err.message,
      };
    }
  } else {
    return {
      address: "",
      status: (
        <span>
          <p>
            {" "}
            🦊{" "}
            <a target="_blank" rel="noreferrer" href={`https://metamask.io/download.html`}>
              You must install Metamask, a virtual Ethereum wallet, in your
              browser.
            </a>
          </p>
        </span>
      ),
    };
  }
};

export const getAccountBalance = async (address) => {
  //input error handling
  if (!window.ethereum || address === null) {
    return {
      status:
        "💡 Connect your Metamask wallet to update the message on the blockchain.",
    };
  }

  const message = await smartContract.methods.balances(address).call();
  return message;

};

export const transferBalance = async (address, transferAddress, amount) => {
  //input error handling
  if (!window.ethereum || address === null || transferAddress === null) {
    return {
      status:
        "💡 Connect your Metamask wallet to update the message on the blockchain.",
    };
  }

  //set up transaction parameters
  const transactionParameters = {
    to: contractAddress, // Required except during contract publications.
    from: address, // must match user's active address.
    data: smartContract.methods.transfer(transferAddress, amount).encodeABI(),
  };

  //sign the transaction
  try {
    const txHash = await window.ethereum.request({
      method: "eth_sendTransaction",
      params: [transactionParameters],
    });
    return {
      status: (
        <span>
          ✅{" "}
          <a target="_blank" rel="noreferrer" href={`https://ropsten.etherscan.io/tx/${txHash}`}>
            View the status of your transaction on Etherscan!
          </a>
        </span>
      ),
    };
  } catch (error) {
    return {
      status: "😥 " + error.message,
    };
  }
};
