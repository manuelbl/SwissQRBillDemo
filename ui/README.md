# QR Bill UI

This project uses React 18, React Router 6, Vite xxx, Typescript 5.2, MUI 5.4 and yarn.


## Development mode

To run the app in development mode, type:

```
yarn dev
```

The app should automatically be available at [http://localhost:5173/qrbill](http://localhost:5173/qrbill) (the link is also displayed in the console).

If you make changes to the source code, the app will automatically reload.

In order to properly work, the [REST service](../service/) should be running (on port 8081) with CORS disabled.


## Unit tests

Unit tests can be run with:

```
yarn test
```

Or for a single run only:

```
yarn ci
```

## Lint

The linter can be run with:

```
yarn lint
```


## Production build

To create the production version of the app, run:

```
yarn build
```

The result will be in the `dist` folder.


## URL structure

The app is built to be run in the subpath `/qrbill` with the REST service at `/qrbill-api`.

In development mode, the development server will redirect URLs starting with `http://localhost:5173/qrbill-api/` to `http://localhost:8081/qrbill-api/` (see [vite.config.ts](vite.config.ts)).
