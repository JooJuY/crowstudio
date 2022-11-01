import React from "react";
import { createBrowserRouter, RouterProvider, Route } from "react-router-dom";

import Header from "./components/Header";
import Intro from "./pages/intro/Intro";
import Login from "./pages/login/Login";
import Signup from "./pages/signup/Signup";
import Main from "./pages/main/Main";
import Mypage from "./pages/mypage/Mypage";
import ProjectCreate from "./pages/project-create/ProjectCreate";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Intro />,
  },
  {
    path: "/login",
    element: <Login />,
  },
  {
    path: "/signup",
    element: <Signup />,
  },
  {
    path: "/main",
    element: <Main />,
  },
  {
    path: "/mypage",
    element: <Mypage />,
  },
  {
    path: "/project/create",
    element: <ProjectCreate />,
  },
]);

function App() {
  return (
    <React.Fragment>
      <Header />
      <RouterProvider router={router} />
    </React.Fragment>
  );
}

export default App;
