import './style/App.css';
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min";
import 'bootstrap-icons/font/bootstrap-icons.css';
import LoginPage from './pages/LoginPage';
import HomePage from './pages/HomePage';
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { UsosTokensProvider } from './contexts/UsosTokensContext';
import CoursePage from './pages/CoursePage';
import TopicPage from './pages/TopicPage';
import AddTopicPage from './pages/AddTopicPage';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <UsosTokensProvider>
          <BrowserRouter>
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/home" element={<HomePage />} />
              <Route path="/course/:courseData" element={<CoursePage />} />
              <Route path="/topic/:topicId" element={<TopicPage />} />
              <Route path="/topic/add/:courseData" element={<AddTopicPage />} />
            </Routes>
          </BrowserRouter>
        </UsosTokensProvider>

      </header>
    </div>
  );
}

export default App;
