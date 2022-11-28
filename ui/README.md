# QR Bill UI

This project uses React 18, React Router 6, Typescript 4.8, MUI 5.4 and yarn.


## Development mode

To run the app in development mode, type:

```
yarn start
```

The app should automatically open in your browser. If not, open [http://localhost:3000](http://localhost:3000).

If you make changes to the source code, the app will automatically reload.

In order to properly work, the [REST service](../service/) should be running (on port 8081).


## Unit tests

Unit tests can be run with:

```
yarn test
```


## Production build

To create the production version of the app, run:

```
yarn build
```

The result will be in the `build` folder.


## URL structure

The app is built to be run in the subpath `/qrbill` with the REST service at `/qrbill-api`.

In development mode, the development server will redirect URLs starting with `http://localhost:3000/qrbill-api/` to `http://localhost:8081/qrbill-api/`. Additionally, for a smooth startup, the URL `http://localhost:3000/` is redirected to `http://localhost:3000/qrbill/`.

The relevant code can be found in [setupProxy.js](setupProxy.js).
