import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import "./style/App.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { UsosTokensProvider } from "./contexts/UsosTokensContext";
import LoginPage from "./pages/LoginPage";
import HomePage from "./pages/HomePage";
import CoursePage from "./pages/CoursePage";
import TopicPage from "./pages/TopicPage";
import AddTopicPage from "./pages/AddTopicPage";
import UserPage from "./pages/UserPage";

function App() {
  return (
    <div className="App">
      <div className="App-container">
        <UsosTokensProvider>
          <BrowserRouter>
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/home" element={<HomePage />} />
              <Route path="/course/:courseData" element={<CoursePage />} />
              <Route path="/topic/:topicId" element={<TopicPage />} />
              <Route path="/topic/add/:courseData" element={<AddTopicPage />} />
              <Route path="/profile" element={<UserPage />} />
            </Routes>
          </BrowserRouter>
        </UsosTokensProvider>
      </div>
    </div>
  );
}

export default App;
