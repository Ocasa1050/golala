#include "CServerManager.h"

#include <stdint.h>

const char* g_szServerNames[MAX_SERVERS] = {
	"My Server",
	"My Server"

};

const CServerInstance::CServerInstanceEncrypted g_sEncryptedAddresses[MAX_SERVERS] = {
	CServerInstance::create("188.127.241.74", 1, 15, 1255, false), // 1
	CServerInstance::create("188.127.241.74", 1, 15, 1255, false) // 2
};
