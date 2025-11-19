package services

import (
	"fmt"
	"time"

	"github.com/darenliang/jikan-go"
	"github.com/k0kubun/pp"
)

type Unloader struct {
}

func NewUnloader() *Unloader {
	return &Unloader{}
}

type DateRange struct {
	From time.Time `json:"from"`
	To   time.Time `json:"to"`
	Prop struct {
		From struct {
			Day   int `json:"day"`
			Month int `json:"month"`
			Year  int `json:"year"`
		} `json:"from"`
		To struct {
			Day   int `json:"day"`
			Month int `json:"month"`
			Year  int `json:"year"`
		} `json:"to"`
		String string `json:"string"`
	} `json:"prop"`
}

type Images3 struct {
	Jpg struct {
		ImageUrl      string `json:"image_url"`
		SmallImageUrl string `json:"small_image_url"`
		LargeImageUrl string `json:"large_image_url"`
	} `json:"jpg"`
	Webp struct {
		ImageUrl      string `json:"image_url"`
		SmallImageUrl string `json:"small_image_url"`
		LargeImageUrl string `json:"large_image_url"`
	} `json:"webp"`
}

type Anime struct {
	MalId          int             `json:"mal_id"`
	Url            string          `json:"url"`
	Images         Images3         `json:"images"`
	Title          string          `json:"title"`
	TitleEnglish   string          `json:"title_english"`
	Genres         []jikan.MalItem `json:"genres"`
	ExplicitGenres []jikan.MalItem `json:"explicit_genres"`
}

func (u *Unloader) Start() {

	anime, err := jikan.GetAnimeById(1)
	if err != nil {
		fmt.Println("error: ", err)
		return
	}

	an := Anime{
		MalId:          anime.Data.MalId,
		Url:            anime.Data.Url,
		Images:         Images3(anime.Data.Images),
		Title:          anime.Data.Title,
		TitleEnglish:   anime.Data.TitleEnglish,
		Genres:         anime.Data.Genres,
		ExplicitGenres: anime.Data.ExplicitGenres,
	}

	pp.Println(an)
}
