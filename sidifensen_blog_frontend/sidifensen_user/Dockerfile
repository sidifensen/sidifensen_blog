FROM nginx

LABEL maintainer="sidifensen_blog_frontend_user 前端用户端"

RUN rm /etc/nginx/conf.d/default.conf

ADD default.conf /etc/nginx/conf.d/

COPY dist/ /usr/share/nginx/html/