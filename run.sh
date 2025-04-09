docker build . -t webcrawler/backend

docker run -e BASE_URL=https://man7.org/linux/man-pages/ -p 4567:4567 --rm webcrawler/backend