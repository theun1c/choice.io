package main

import (
//	"fmt"

//	"github.com/supabase-community/supabase-go"
	"github.com/theun1c/unloaded-service_choice.io/services"
)



func main() {

	// client, err := supabase.NewClient(API_URL, API_KEY, nil) //&supabase.ClientOptions{})
	// if err != nil {
	// 	fmt.Println("Failed to init the client", err)
	// }

  un := services.NewUnloader()
  un.Start()

}
