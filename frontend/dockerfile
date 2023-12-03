FROM node:16

COPY package*.json ./
COPY . .

RUN yarn install

CMD ["yarn", "start"]
