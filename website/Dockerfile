FROM node:20-alpine AS build

WORKDIR /app

COPY website/package.json website/package-lock.json* ./
RUN npm ci || npm install

COPY website/ .

ARG NEXT_PUBLIC_API_BASE_URL=http://server:8080
ENV NEXT_PUBLIC_API_BASE_URL=${NEXT_PUBLIC_API_BASE_URL}

RUN npm run build

FROM node:20-alpine

WORKDIR /app

COPY --from=build /app/.next ./.next
COPY --from=build /app/public ./public
COPY --from=build /app/package.json ./package.json
COPY --from=build /app/node_modules ./node_modules
COPY --from=build /app/next.config.mjs ./next.config.mjs

ENV PORT=3000
EXPOSE 3000

CMD ["npm", "start"]
