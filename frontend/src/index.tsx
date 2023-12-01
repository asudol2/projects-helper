import React from 'react';
import ReactDOM from 'react-dom/client';
import './style/index.css';
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min";
import 'bootstrap-icons/font/bootstrap-icons.css';
import App from './App';

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);
document.title = "Zapisy na przedmioty"
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

