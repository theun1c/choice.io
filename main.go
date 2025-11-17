package main

import (
	"fmt"
	"time"

	"github.com/darenliang/jikan-go"
	"github.com/k0kubun/pp"
)

func main() {
	jikan.Client.Timeout = time.Second * 10
	for i := 1; i <= 10; i++ {
		time.Sleep(10 * time.Second)
		anime, err := jikan.GetAnimeById(i)
		if err != nil {
			fmt.Println(err)
		} else {
			pp.Println(anime.Data)
		}

	}
}
