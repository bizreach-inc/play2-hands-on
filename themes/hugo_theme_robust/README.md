# What is this.

This is the grid based theme for Hugo.

[Hugo :: A fast and modern static website engine](https://gohugo.io/)

## PC View

![screenshot](https://raw.githubusercontent.com/dim0627/hugo_theme_robust/master/images/screenshot.png)

## SP View(Responsive)

![screenshot](https://raw.githubusercontent.com/dim0627/hugo_theme_robust/master/images/responsive.png)

# Features

* Responsive design
* Google Analytics
* Thumbnail
* Share button
* Structured data(Article and Breadcrumb)
* Twitter cards
* OGP
* Disqus
* Syntax Highlight

## Installation

```
$ cd themes
$ git clone https://github.com/dim0627/hugo_theme_robust.git
```

[Hugo \- Installing Hugo](http://gohugo.io/overview/installing/)

# `config.toml` example

```
baseurl = "https://example.com/"
title = "SiteTitle"

googleAnalytics = "UA-XXXXXXXX-XX" # Optional
disqusShortname = "XYW"

[params]
  dateformat = "Jan 2, 2006" # Optional
  # Fonts settings.
  googlefonts = "https://fonts.googleapis.com/css?family=Lobster|Lato:400,700" # Optional, Include google fonts.
  fontfamily = "Lato,YuGothic,'Hiragino Kaku Gothic Pro',Meiryo,sans-serif" # Optional, Override body font family.
  logofontfamily = "Lobster, cursive" # Optional, Override logo font.
```

# Frontmatter example

```
+++
date = "2016-09-28T17:00:00+09:00"
title = "Article title here"
thumbnail = "images/thumbnail.jpg" # Optional, referenced at `$HUGO_ROOT/static/images/thumbnail.jpg`
+++
```

# Shortcodes

## Image

```
{{% img src="images/image.jpg" %}}
{{% img src="images/image.jpg" class="right" %}}
{{% img src="images/image.jpg" class="left" %}}
{{% img src="images/image.jpg" caption="Referenced from wikipedia." href="https://en.wikipedia.org/wiki/Lorem_ipsum" %}}
```

![screenshot](https://raw.githubusercontent.com/dim0627/hugo_theme_robust/master/images/include-images.png)

## Clear

Break float.

```
{{% img src="images/image.jpg" class="right" %}}

brabrabra # Displayed left of the image.

{{% clear %}}

brabrabra # Displayed below of the image.
```

# Development mode

Supported development mode.

```
env HUGO_ENV="DEV" hugo server --watch --buildDrafts=true --buildFuture=true -t robust
```

This mode is

* Not show Google Analytics tags.
* Show `IsDraft`.
* Show `WordCount`.

And set `{{ if ne (getenv "HUGO_ENV") "DEV" }} Set elements here. {{ end }}` if you want to place only in a production environment.

