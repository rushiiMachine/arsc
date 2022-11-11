#include <std/core.pat>
#pragma pattern_limit 500000

enum HeaderType : u16 {
	StringPool = 0x01,
	Table = 0x02,
	TablePackage = 0x0200,
	TableType = 0x0201,
	TableTypeSpec = 0x0202,
	TableLibrary = 0x0203,
};

struct Header {
	HeaderType type;
	u16 headerSize;
	u32 size;
};

struct utf8_string {
	u8 bytes1;
	if (bytes1 & 0x80 != 0) {
		u8 bytes2;
	}

	u8 chars1;
	if (chars1 & 0x80 != 0) {
		u8 chars2;
		char string[((chars1 & 0x7F) << 8) | chars2];
	} else {
		char string[chars1];
	}

	u8 end;
};

struct utf16_string {
	u16 length;
	char16 string[length];
	u16 null_term;
};

// not sure if this works
// also is linear so fix later
fn readSpans() {
	while(true) {
		u32 name;

		if (name == 0xFFFFFFFF)
			break;

		u32 start;
		u32 end;
	}
};

struct Style {
	readSpans();
};

struct StringPool {
	Header header;
	u32 stringsCount;
	u32 stylesCount;
	u32 flags;
	u32 stringsOffset;
	u32 stylesOffset;
	u32 stringsOffsets[stringsCount];
	u32 stylesOffsets[stylesCount];
	if (flags & 0x0100 != 0) {
		utf8_string strings[stringsCount];
	} else {
		utf16_string strings[stringsCount];
	}
	Style styles[stylesCount];

	// not sure if this is correct
	u8 alignment[$ % 4];
};

struct TypeSpec {
	u8 typeId;
	u8 res0;
	u16 res1;
	u32 specCount;
	u32 flags[specCount];
};

struct TypeConfigId {
	u32 size;
	$ += -0x04;
	u8 idBytes[size];
};

struct Value {
	u16 size;
	u8 zero;
	u8 type;
	u32 data;
};

struct BagValueEntry {
	u32 index;
	Value value;
};

struct BagValue {
	u32 parentId;
	u32 count;
	BagValueEntry values[count];
};

struct ResourceEntry {
	u16 size;
	u16 flags;
	u32 nameIndex;
	if (flags & 0x0001 != 0) {
		BagValue value;
	} else {
		Value value;
	}
};

fn countValidRscs(ref auto entryUnknown) {
	u32 count = 0;
	u32 entriesCount = std::core::member_count(entryUnknown);

	for (u32 i = 0, i < entriesCount, i = i + 1) {
		if (entryUnknown[i] != 0xFFFFFFFF)
			count = count + 1;
	}

	return count;
};

struct TypeConfig {
	u8 typeId;
	u8 res0;
	u16 res1;
	u32 resourcesCount;
	u32 resourcesStart;
	TypeConfigId id;
	// figure out what this is
	u32 entryUnknown[resourcesCount];
	ResourceEntry resources[countValidRscs(entryUnknown)];
};

struct Chunk {
	Header header;

	if (header.type == HeaderType::TableTypeSpec)
		TypeSpec spec;
	else if (header.type == HeaderType::TableType)
		TypeConfig config;
	else
		u8 unknown[header.size - sizeof(header)];
};

struct Package {
	Header header;
	u32 packageId;
	char16 packageName[128];
	u32 typeStringsOffset;
	u32 lastPublicType;
	u32 keyStringsOffset;
	u32 lastPublicKey;
	u32 typeIdsOffset;
	StringPool typeNames;
	StringPool keyNames;

	// i couldnt figure out a way to make this dynamic
	Chunk chunks[94];
};

struct Arsc {
	Header header;
	u32 packageCount;
	StringPool globalPool;
	Package packages[packageCount];
};

Arsc arsc @ 0;
