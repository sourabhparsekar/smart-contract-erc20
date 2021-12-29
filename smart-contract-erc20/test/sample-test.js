const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("Token Contract", function () {
  it("Should return the contract name", async function () {
    const Token = await ethers.getContractFactory("Token");
    const token = await Token.deploy(100);
    await token.deployed();

    expect(await token.name()).to.equal("ERC20-Demo-Contract");
  });
});
