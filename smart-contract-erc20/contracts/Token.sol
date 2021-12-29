// SPDX-License-Identifier: MIT

pragma solidity ^0.8.0;

import "./IERC20.sol";

// Sample Code taken from https://ethereum.org/en/developers/tutorials/understand-the-erc-20-token-smart-contract/

contract Token is IERC20 {
    string public constant name = "ERC20-Demo-Contract";
    string public constant symbol = "ERC-Demo-Contract";
    uint8 public constant decimals = 2; // 18 is default decimal places, for simplicity it is considered as 2 

    mapping(address => uint256) public balances;

    mapping(address => mapping(address => uint256)) public allowed;

    uint256 public totalSupply_;

    constructor(uint256 total) {
        totalSupply_ = total;
        balances[msg.sender] = totalSupply_;
    }

    function totalSupply() public view override returns (uint256) {
        return totalSupply_;
    }

    function balanceOf(address tokenOwner)
        public
        view
        override
        returns (uint256)
    {
        return balances[tokenOwner];
    }

    function transfer(address receiver, uint256 numTokens)
        public
        override
        returns (bool)
    {
        require(
            numTokens <= balances[msg.sender],
            "Balance is less than available."
        );
        balances[msg.sender] = balances[msg.sender] - (numTokens);
        balances[receiver] = balances[receiver] + (numTokens);
        emit Transfer(msg.sender, receiver, numTokens);
        return true;
    }

    function approve(address delegate, uint256 numTokens)
        public
        override
        returns (bool)
    {
        allowed[msg.sender][delegate] = numTokens;
        emit Approval(msg.sender, delegate, numTokens);
        return true;
    }

    function allowance(address owner, address delegate)
        public
        view
        override
        returns (uint256)
    {
        return allowed[owner][delegate];
    }

    function transferFrom(
        address owner,
        address buyer,
        uint256 numTokens
    ) public override returns (bool) {
        require(
            numTokens <= balances[owner],
            "Balance is less than available."
        );
        require(
            numTokens <= allowed[owner][msg.sender],
            "Qty is less than allowance."
        );

        balances[owner] = balances[owner] - (numTokens);
        allowed[owner][msg.sender] = allowed[owner][msg.sender] - (numTokens);
        balances[buyer] = balances[buyer] + (numTokens);
        emit Transfer(owner, buyer, numTokens);
        return true;
    }
}
