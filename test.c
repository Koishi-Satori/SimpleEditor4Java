int main () {
    test(114514);
    return 0;
}

bool test (int num) {
    bool ignore[num + 1];
    memset(ignore, true, (num + 1) * sizeof(bool));
    //TODO:just a test.
    return false;
}