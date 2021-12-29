import React from "react";
import { useEffect, useState } from "react";
import {
  connectWallet,
  getCurrentWalletConnected,
  loadContractName,
  loadContractSymbol,
  loadContractTotalSupply
} from "./util/interact.js";

import alchemylogo from "./alchemylogo.svg";

const SmartContract = () => {
  //state variables
  const [walletAddress, setWallet] = useState("");
  const [status, setStatus] = useState("");
  const [name, setName] = useState("No connection to the network."); //default message
  const [symbol, setSymbol] = useState("No connection to the network."); //default message
  const [totalSupply, setTotalSupply] = useState("No connection to the network."); //default message
  const [newMessage, setNewMessage] = useState("");

  //called only once
  useEffect(() => {

    async function setup() {
      const name = await loadContractName();
      setName(name);

      const symbol = await loadContractSymbol();
      setSymbol(symbol);

      const totalSupply = await loadContractTotalSupply();
      setTotalSupply(totalSupply);


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
          setStatus("👆🏽 Write a message in the text-field above.");
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

  const onUpdatePressed = async () => {
    // const { status } = await updateMessage(walletAddress, newMessage);
    // setStatus(status);
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

      <p style={{ paddingTop: "50px" }}><b>Token Name:</b> {name}</p>
      <p><b>Token Symbol:</b> {symbol}</p>
      <p><b>Total Supply:</b> {totalSupply}</p>

      <h2 style={{ paddingTop: "18px" }}>New Message:</h2>

      <div>
        <input
          type="text"
          placeholder="Update the message in your smart contract."
          onChange={(e) => setNewMessage(e.target.value)}
          value={newMessage}
        />
        <p id="status">{status}</p>

        <button id="publish" onClick={onUpdatePressed}>
          Update
        </button>
      </div>
    </div>
  );
};

export default SmartContract;
