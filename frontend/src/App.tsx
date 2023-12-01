import './style/App.css';
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min";
import 'bootstrap-icons/font/bootstrap-icons.css';
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
            <Route path="/home" element={<HomePage />}/>
          </Routes>
        </BrowserRouter>
      </header>
    </div>
  );
}

export default App;
