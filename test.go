package main

import "fmt"

func main () {
    var num int
    while (true) {
        _, err = fmt.Scanf();
        if err != nil {
            return
        }
        fmt.Scan(&num);
        fmt.Println("num:", num);
    }
}