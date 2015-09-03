#ifndef SCAPI_DLOG_CryptoPP_H
#define SCAPI_DLOG_CryptoPP_H


#include "Dlog.hpp"
// CryptoPP includes
#include <gfpcrypt.h>
#include <random>
#include <cryptlib.h>
#include <osrng.h>

/**********************/
/**** Helpers *********/
/**********************/
biginteger cryptoppint_to_biginteger(CryptoPP::Integer cint);
CryptoPP::Integer biginteger_to_cryptoppint(biginteger bi);
/**
* This class is an adapter class of Crypto++ to a ZpElement in SCAPI.<p>
* It holds a pointer to a Zp element in Crypto++. It implements all the functionality of a Zp element.
*/
class ZpSafePrimeElementCryptoPp : public ZpSafePrimeElement {
private:
	biginteger element;
public:
	/**
	* This constructor accepts x value and DlogGroup (represented by p).
	* If x is valid, sets it; else, throws exception
	* @param x
	*  @param p - group modulus
	* @throws IllegalArgumentException
	*/
	ZpSafePrimeElementCryptoPp(biginteger x, biginteger p, bool bCheckMembership);
	/**
	* Constructor that gets DlogGroup and chooses random element with order q.
	* The algorithm is:
	* input: modulus p
	* choose a random element between 1 to p-1
	* calculate element^2 mod p
	* @param p - group modulus
	*/
	ZpSafePrimeElementCryptoPp::ZpSafePrimeElementCryptoPp(biginteger p, mt19937 prg);
	/*
	* Constructor that simply create element using the given value
	*/
	ZpSafePrimeElementCryptoPp::ZpSafePrimeElementCryptoPp(biginteger elementValue);

	biginteger getElementValue() override{
		return element;
	}
	/**
	* This function checks if this element is the identity of the Dlog group.
	* @return <code>true</code> if this element is the identity of the group; <code>false</code> otherwise.
	*/
	bool isIdentity() override { return element == 1; }
	bool ZpSafePrimeElementCryptoPp::operator==(const GroupElement &other) const override;
	bool ZpSafePrimeElementCryptoPp::operator!=(const GroupElement &other) const override;
	string toString() {
		return "ZpSafePrimeElementCryptoPp [element value=" + string(element) + "]";
	}
	GroupElementSendableData * generateSendableData() override;
};

/**
* This class implements a Dlog group over Zp* utilizing Crypto++'s implementation.<p>
*/
class CryptoPpDlogZpSafePrime :public DlogGroupAbs, public DDH {//public DlogZpSafePrime, 
private:
	CryptoPP::DL_GroupParameters_GFP_DefaultSafePrime * pointerToGroup = NULL; // pointer to the native group object
	int calcK(biginteger p);

protected:
	/**
	* deletes the related Dlog group object
	*/
	virtual ~CryptoPpDlogZpSafePrime();

public:
	/**
	* Initializes the CryptoPP implementation of Dlog over Zp* with the given groupParams
	* @param groupParams - contains the group parameters
	*/
	CryptoPpDlogZpSafePrime(ZpGroupParams * groupParams, mt19937 prg = mt19937(clock()));

	/**
	* Initializes the CryptoPP implementation of Dlog over Zp* with the given groupParams
	* @param q the order of the group
	* @param g the generator of the group
	* @param p the prime of the group
	*/
	CryptoPpDlogZpSafePrime(string q, string g, string p) : CryptoPpDlogZpSafePrime(new ZpGroupParams(biginteger(q), biginteger(g), biginteger(p))) {};

	/**
	* Initializes the CryptoPP implementation of Dlog over Zp* with the given groupParams
	* @param q the order of the group
	* @param g the generator of the group
	* @param p the prime of the group
	* @throws NoSuchAlgorithmException
	*/
	CryptoPpDlogZpSafePrime(string q, string g, string p, string randNumGenAlg) { throw invalid_argument("not implemented"); };

	/**
	* Initializes the CryptoPP implementation of Dlog over Zp* with random elements
	* @param numBits - number of the prime p bits to generate
	*/
	CryptoPpDlogZpSafePrime(int numBits = 1024, mt19937 prg = mt19937(clock()));

	//creates an int from the given string and calls the appropriate constructor
	CryptoPpDlogZpSafePrime(string numBits) : CryptoPpDlogZpSafePrime(stoi(numBits)) {};

	CryptoPpDlogZpSafePrime(string numBits, string randNumGenAlg) {
		// TODO: NOT IMPLEMENTED 
	}

	/**
	* @return the type of the group - Zp*
	*/
	string getGroupType() { return "Zp*"; }

	/**
	* @return the identity of this Zp group - 1
	*/
	GroupElement * getIdentity() {
		return new ZpSafePrimeElementCryptoPp(1, ((ZpGroupParams *)groupParams)->getP(), false);
	}

	GroupElement * createRandomElement() override {
		//This function overrides the basic implementation of DlogGroupAbs. For the case of Zp Safe Prime this is a more efficient implementation.
		//It calls the package private constructor of ZpSafePrimeElementCryptoPp, which randomly creates an element in Zp.
		return new ZpSafePrimeElementCryptoPp(((ZpGroupParams *)groupParams)->getP(), random_element_gen);
	}

	bool isMember(GroupElement * element) override; 
	bool isGenerator() override;
	bool validateGroup() override;
	GroupElement * getInverse(GroupElement * groupElement) override;
	GroupElement * exponentiate(GroupElement * base, biginteger exponent) override;
	GroupElement * multiplyGroupElements(GroupElement * groupElement1, GroupElement * groupElement2) override;
	GroupElement * simultaneousMultipleExponentiations(vector<GroupElement *> groupElements, vector<biginteger> exponentiations) override; 
	GroupElement * generateElement(bool bCheckMembership, vector<biginteger> values) override;
	GroupElement * encodeByteArrayToGroupElement(const vector<unsigned char> & binaryString) override;
	const vector<unsigned char> decodeGroupElementToByteArray(GroupElement * groupElement) override;
	virtual const vector<unsigned char>  mapAnyGroupElementToByteArray(GroupElement * groupElement) override;
	virtual GroupElement * reconstructElement(bool bCheckMembership, GroupElementSendableData * data) override;
};

#endif