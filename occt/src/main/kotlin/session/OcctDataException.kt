package session


class OcctDataException(field: String, expected: Int, actual: Int) :
    RuntimeException("Malformed native data for '$field': expected $expected elements, got $actual")