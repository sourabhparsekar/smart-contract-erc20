import React from "react";
import { useEffect, useState } from "react";
import {
  connectWallet,
  getCurrentWalletConnected,
  loadContractName,
  loadContractSymbol,
  loadContractTotalSupply,
  loadContractDecimals,
  getAccountBalance,
  transferBalance
} from "./util/interact.js";

import alchemylogo from "./alchemylogo.svg";

const SmartContract = () => {
  //state variables
  const [walletAddress, setWallet] = useState("");
  const [status, setStatus] = useState("");
  const [balanceAddress, setBalanceAddress] = useState("");
  const [balanceStatus, setBalanceStatus] = useState("");
  const [transferAddress, setTransferAddress] = useState("");
  const [transferAmount, setTransferAmount] = useState("");
  const [transferStatus, setTransferStatus] = useState("");
  const [name, setName] = useState("No connection to the network."); //default message
  const [symbol, setSymbol] = useState("No connection to the network."); //default message
  const [totalSupply, setTotalSupply] = useState("No connection to the network."); //default message
  const [decimals, setDecimals] = useState("No connection to the network."); //default message

  //called only once
  useEffect(() => {

    async function setup() {
      const name = await loadContractName();
      setName(name);

      const symbol = await loadContractSymbol();
      setSymbol(symbol);

      const totalSupply = await loadContractTotalSupply();
      setTotalSupply(totalSupply);

      const decimals = await loadContractDecimals();
      setDecimals(decimals);

      const { address, status } = await getCurrentWalletConnected();

      setWallet(address);

      setStatus(status);

      addWalletListener();

    }
    setup();
  }, []);


  function addWalletListener() {
    if (window.ethereum) {
      window.ethereum.on("accountsChanged", (accounts) => {
        if (accounts.length > 0) {
          setWallet(accounts[0]);
          setStatus("👆🏽 Populate the Data and Click on Button to execute...");
        } else {
          setWallet("");
          setStatus("🦊 Connect to Metamask using the top right button.");
        }
      });
    } else {
      setStatus(
        <p>
          {" "}
          🦊{" "}
          <a target="_blank" rel="noreferrer" href={`https://metamask.io/download.html`}>
            You must install Metamask, a virtual Ethereum wallet, in your
            browser.
          </a>
        </p>
      );
    }
  }

  const connectWalletPressed = async () => {
    const walletResponse = await connectWallet();
    setStatus(walletResponse.status);
    setWallet(walletResponse.address);
  };

  const onGetBalancePressed = async () => {
    const status = await getAccountBalance(balanceAddress);
    setBalanceStatus(status);
  };

  const onTransferBalancePressed = async () => {
    const { status } = await transferBalance(walletAddress, transferAddress, transferAmount);
    setTransferStatus(status);
  };

  //the UI of our component
  return (
    <div id="container">
      <img id="logo" src={alchemylogo} alt="logo"></img>
      <button id="walletButton" onClick={connectWalletPressed}>
        {walletAddress.length > 0 ? (
          "Connected: " +
          String(walletAddress).substring(0, 6) +
          "..." +
          String(walletAddress).substring(38)
        ) : (
          <span>Connect Wallet</span>
        )}
      </button>

      <p style={{ paddingTop: "50px" }}><b>Token Name:</b> {name} &nbsp;&nbsp; <b>Token Symbol:</b> {symbol}</p>
      <p><b>Total Supply:</b> {totalSupply} &nbsp;&nbsp; <b>Decimals:</b> {decimals}</p>
      <p id="status">{status}</p>

      <h2 style={{ paddingTop: "5px" }}>Get Balance:</h2>
      <div>
        <input
          type="text"
          placeholder="Enter Wallet address 0x..."
          onChange={(e) => setBalanceAddress(e.target.value)}
          value={balanceAddress}
        />
        <p id="status">{balanceStatus}</p>

        <button id="publish" onClick={onGetBalancePressed}>
          Get Balance
        </button>
      </div>

      <h2 style={{ paddingTop: "5px" }}>Transfer Balance:</h2>
      <div>
        <input
          type="text"
          placeholder="Enter Wallet address 0x..."
          onChange={(e) => setTransferAddress(e.target.value)}
          value={transferAddress}
        />
        <input
          type="text"
          placeholder="Enter Amount to be transferred"
          onChange={(e) => setTransferAmount(e.target.value)}
          value={transferAmount}
        />
        <p id="status">{transferStatus}</p>

        <button id="publish" onClick={onTransferBalancePressed}>
          Transfer Balance
        </button>
      </div>


    </div>
  );
};

export default SmartContract;
