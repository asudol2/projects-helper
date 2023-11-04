import './App.css';
import LoginPage from './pages/LoginPage';
import HomePage from './pages/HomePage';
import { BrowserRouter, Routes, Route } from "react-router-dom";

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<LoginPage />}/>
            <Route path="/login" element={<LoginPage />}/>
            <Route path="/home/:usosAccessToken/:usosAccessSecret" element={<HomePage />}/>
          </Routes>
        </BrowserRouter>
      </header>
    </div>
  );
}

export default App;
